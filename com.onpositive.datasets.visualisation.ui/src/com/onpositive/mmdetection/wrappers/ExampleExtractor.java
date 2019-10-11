package com.onpositive.mmdetection.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.python.pydev.core.IGrammarVersionProvider;
import org.python.pydev.core.MisconfigurationException;
import org.python.pydev.parser.PyParser;
import org.python.pydev.shared_core.model.ISimpleNode;
import org.python.pydev.shared_core.parsing.BaseParser;
import org.python.pydev.parser.jython.ast.Assign;
import org.python.pydev.parser.jython.ast.Call;
import org.python.pydev.parser.jython.ast.Module;
import org.python.pydev.parser.jython.ast.Name;
import org.python.pydev.parser.jython.ast.NameTok;
import org.python.pydev.parser.jython.ast.Str;
import org.python.pydev.parser.jython.ast.exprType;
import org.python.pydev.parser.jython.ast.keywordType;
import org.python.pydev.parser.jython.ast.stmtType;

public class ExampleExtractor {
	
	public List<MMDetCfgData> processFile(String filePath) {
		IWorkspace ws = ResourcesPlugin.getWorkspace();		
		IFolder file = ws.getRoot().getFolder(new Path(filePath));
		IFolder root = file;
		
		BaseParser parser = this.createParser();
		IDocumentProvider provider = new TextFileDocumentProvider();
		
		
		return processFile(file, new ArrayList<MMDetCfgData>(), root, parser, provider);
	}
	
	public List<MMDetCfgData> processFile(IResource file, List<MMDetCfgData> result, IFolder root, BaseParser parser, IDocumentProvider provider) {
		
		if(file instanceof IFolder) {
			IResource[] children;
			try {
				children = ((IFolder)file).members();
				for(int i = 0 ; i < children.length ; i++) {
					IResource chFile = children[i];
					processFile(chFile, result, root, parser, provider);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		else if(file instanceof IFile && file.getName().endsWith(".py")) {
			try {
				MMDetCfgData cfg = this.tryProcessConfigFile((IFile)file, root, parser, provider);
				if(cfg != null) {
					result.add(cfg);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}		
		return result;		
	}

	private MMDetCfgData tryProcessConfigFile(IFile file, IFolder root, BaseParser parser, IDocumentProvider provider) throws CoreException {
		
		
		try {
			provider.connect(file);
			IDocument document = provider.getDocument(file);
			parser.setDocument(document, file);
			parser.reparseDocument();
			ISimpleNode astRoot = parser.getRoot();
			if( !(astRoot instanceof Module) ) {
				return null;
			}
			Module module = (Module) astRoot;
			stmtType[] statements = module.body;
		
			DictWrapper model = findDict(statements, "model");
			if(model == null) {
				return null;
			}
			String modelType = model.getStringValue("type");
			if(modelType == null) {
				return null;
			}
			String relPath = file.getFullPath().makeRelativeTo(root.getFullPath()).toOSString().replace("\\", "/");
			MMDetCfgData result = new MMDetCfgData(modelType, relPath);
			
			Map<String, List<DictWrapper>> chMap
				= model.getChildren().stream().collect(Collectors.groupingBy(x->x.getName()));
	
			chMap.entrySet().stream().forEach(e->{
				String paramName = e.getKey();
				List<String> lst = e.getValue().stream().map(x->x.getStringValue("type")).sorted().collect(Collectors.toList());
				String paramValue = String.join(", ", lst);
				result.addParameter(paramName, paramValue);								
			});	
			return result;
		} finally {
			provider.disconnect(file);
		}
		
	}
	
	private DictWrapper findDict(stmtType[] statements, String name) {
		DictWrapper result = null;
		for(stmtType st : statements) {
			if(!(st instanceof Assign)) {
				continue;
			}			
			Assign as = (Assign)st;
			exprType[] targets = as.targets;
			if(targets.length != 1 || !(targets[0] instanceof Name)) {
				continue;
			}
			String n = ((Name)targets[0]).id;
			if(!n.equals(name)) {
				continue;
			}
			exprType val = as.value;
			if(!(val instanceof Call)) {
				continue;
			}
			Call ce = (Call) val;
			exprType func = ce.func;
			if(!(func instanceof Name)){
				continue;
			}
			String fName = ((Name)func).id;
			if(!fName.equals("dict")) {
				continue;
			}
			result = new DictWrapper(name, ce);			
		}
		return result;
	}
	
	private static class DictWrapper{
		
		public DictWrapper(String name, Call exp) {
			super();
			this.name = name;
			this.exp = exp;
		}

		private String name;
		
		private Call exp;
		
		public String getName() {
			return name;
		}
		
		public String getStringValue(String key) {
			Optional<keywordType> theArg = Arrays.asList(exp.keywords).stream()
					.filter(x->(x.arg instanceof NameTok) && ((NameTok)x.arg).id.equals(key)).findFirst();
			if(theArg.isEmpty()) {
				return null;
			}
			exprType exp = theArg.get().value;
			if(!(exp instanceof Str)){
				return null;
			}
			String result = ((Str)exp).s;
			return result;
		}
		
		public List<DictWrapper> getChildren(){
			List<String> names = new ArrayList<>();		
			List<Call> dicts = new ArrayList<>();
			for (keywordType x : exp.keywords) {
				if (!(x.arg instanceof NameTok)) {
					continue;
				}
				String argName = ((NameTok)x.arg).id;
				
				Call ce = asDictionary(x.value);				
				if (ce != null) {
					names.add(argName);
					dicts.add(ce);
				}
				else if (x.value instanceof org.python.pydev.parser.jython.ast.List) {
					exprType[] elts = ((org.python.pydev.parser.jython.ast.List)x.value).elts;
					List<Call> dicts1 = Arrays.asList(elts).stream().map(y->asDictionary(y))
							.filter(y->y!=null).collect(Collectors.toList());
					
					if(elts.length == dicts1.size()) {
						dicts1.forEach(d->{
							names.add(argName);
							dicts.add(d);
						});
					}					
				}
				
			}
			List<DictWrapper> result = new ArrayList<DictWrapper>();
			for(int i = 0 ; i < names.size(); i++) {
				String n = names.get(i);
				Call dict = dicts.get(i);
				result.add(new DictWrapper(n, dict));
			}
			return result;
		}
		
		private Call asDictionary(exprType expr) {
			if (!(expr instanceof Call)) {
				return null;
			}
			exprType func = ((Call) expr).func;
			if (!(func instanceof Name)) {
				return null;
			}
			String fName = ((Name) func).id;
			if(!fName.equals("dict")) {
				return null;
			}	
			return (Call)expr;				
		}
		
	}
	
	public List<String> gatherWeightPaths(String filePath) {
		IWorkspace ws = ResourcesPlugin.getWorkspace();		
		IFolder file = ws.getRoot().getFolder(new Path(filePath));
		IFolder root = file;		
		
		return gatherWeightPaths(file, new ArrayList<String>(), root);
	}
	
	public List<String> gatherWeightPaths(IResource file, List<String> result, IFolder root) {
		
		if(file instanceof IFolder) {
			IResource[] children;
			try {
				children = ((IFolder)file).members();
				for(int i = 0 ; i < children.length ; i++) {
					IResource chFile = children[i];
					gatherWeightPaths(chFile, result, root);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		else if(file instanceof IFile && file.getName().endsWith(".pth")) {
			String relPath = file.getFullPath().makeRelativeTo(root.getFullPath()).toOSString().replace("\\", "/");
			result.add(relPath);
		}		
		return result;		
	}
	
	
	private BaseParser createParser() {
		return new PyParser(new DummyGrammarVersionProvider());
	}
	
	static class DummyGrammarVersionProvider implements IGrammarVersionProvider{

		@Override
		public AdditionalGrammarVersionsToCheck getAdditionalGrammarVersions() throws MisconfigurationException {
			return null;
		}

		@Override
		public int getGrammarVersion() throws MisconfigurationException {
			return IGrammarVersionProvider.GRAMMAR_PYTHON_VERSION_3_6;
		}
		
	}

}
