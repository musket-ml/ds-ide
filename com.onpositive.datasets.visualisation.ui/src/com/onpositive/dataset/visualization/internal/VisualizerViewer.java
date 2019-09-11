package com.onpositive.dataset.visualization.internal;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Control;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IDataSet;

public abstract class VisualizerViewer<T extends Control> extends AbstractUIElement<T>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static LinkedBlockingDeque<Runnable> tasks = new LinkedBlockingDeque<>();
	protected IAnalizeResults input;

	static {
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					try {
						Runnable pollLast = tasks.pollLast(1000, TimeUnit.MINUTES);
						try {
							pollLast.run();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	public void setInput(IAnalizeResults noneAnalisys) {
		this.input=noneAnalisys;
	}
	public IAnalizeResults getInput() {
		return this.input;
	}
	
	public static class It {
		Integer data;

		public It(Integer data, IDataSet ds) {
			super();
			this.data = data;
			this.ds = ds;
		}

		IDataSet ds;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((data == null) ? 0 : data.hashCode());
			result = prime * result + ((ds == null) ? 0 : System.identityHashCode(ds));
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
			It other = (It) obj;
			if (data == null) {
				if (other.data != null)
					return false;
			} else if (!data.equals(other.data))
				return false;
			if (ds == null) {
				if (other.ds != null)
					return false;
			} else if (ds != other.ds)
				return false;
			return true;
		}
	}

}
