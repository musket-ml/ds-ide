package com.onpositive.dside.ui.editors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import org.aml.typesystem.IStatusVisitor;
import org.aml.typesystem.Status;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.yaml.snakeyaml.nodes.NodeTuple;

import com.onpositive.dside.ui.builder.SampleBuilder;
import com.onpositive.yamledit.ast.IHasLocation;

class ErrorVisitor implements IStatusVisitor {

	Stack<IHasLocation> stack = new Stack<>();
	protected IFile file;
	private String content;

	public ErrorVisitor(IFile file,String content) {
		super();
		this.file = file;
		this.content=content;
	}

	class ErrorAndMessage {
		IHasLocation element;
		String message;
		String key;
		boolean onKey;

		public void report() {
			try {
				IMarker marker = file.createMarker("de.jcup.yamleditor.script.problem");
				int start = 0;
				int end = 0;
				if (element != null) {
					IHasLocation peek = element;
					start = peek.getStartOffset();
					if (peek.getParent() == null) {
						end = start;
					} else {
						end = peek.getEndOffset();
					}
					if (key!=null) {
						NodeTuple findInKey = element.findInKey(key);
						if (findInKey!=null) {
							start=findInKey.getValueNode().getStartMark().getIndex();
							end=findInKey.getValueNode().getEndMark().getIndex();
							if (onKey) {
								start=findInKey.getKeyNode().getStartMark().getIndex();
								end=findInKey.getKeyNode().getEndMark().getIndex();
							}
						}
						
					}
				}
				end=adjustEnd(start,end);
				start=adjustStart(start);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute(IMarker.CHAR_START, start);
				marker.setAttribute(IMarker.CHAR_END, end);
				marker.setAttribute(IMarker.LOCATION, file.getFullPath().toPortableString());
				marker.setAttribute(IMarker.MESSAGE, message);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public ErrorAndMessage(IHasLocation element, String message, String key,boolean onKey) {
			super();
			this.element = element;
			this.message = message;
			this.key = key;
			this.onKey=onKey;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((element == null) ? 0 : element.hashCode());
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((message == null) ? 0 : message.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ErrorAndMessage other = (ErrorAndMessage) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (element == null) {
				if (other.element != null)
					return false;
			} else if (!element.equals(other.element))
				return false;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (message == null) {
				if (other.message != null)
					return false;
			} else if (!message.equals(other.message))
				return false;
			return true;
		}

		private ErrorVisitor getOuterType() {
			return ErrorVisitor.this;
		}

	}

	public HashSet<ErrorAndMessage> messages = new HashSet<>();

	@Override
	public void startVisiting(Status st) {
		try {
			
			Object source = st.getSource();
			if (source instanceof IHasLocation) {
				stack.push((IHasLocation) source);
			}
			

			if (stack.size() > 0) {
				if (!st.isOk()) {
				IHasLocation peek = stack.peek();
				ErrorAndMessage e = new ErrorAndMessage(peek, st.getMessage(), st.getKey(),st.isOnKey());
				for (ErrorAndMessage m : new ArrayList<>(this.messages)) {
					if (m.message.equals(st.getMessage())) {
						IHasLocation z = peek;
						while (z != null) {
							if (z.equals(m.element)) {
								this.messages.remove(m);
							}
							z = z.getParent();
						}
					}
				}
				
					messages.add(e);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int adjustStart(int start) {
		return start;
	}

	public int adjustEnd(int start,int end) {
		if (end>=content.length()) {
			end=content.length()-1;
		}
		while (end>start) {
			char charAt = content.charAt(end);
			if (!Character.isWhitespace(charAt)&&charAt!='-') {
				return end+1;
			}
			end--;
		}
		return end;
	}

	@Override
	public void endVisiting(Status st) {
		Object source = st.getSource();
		if (source instanceof IHasLocation) {
			stack.pop();
			if (stack.isEmpty()) {
				this.messages.forEach(v -> v.report());
			}
		}
	}

}