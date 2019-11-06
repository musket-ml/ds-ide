package com.onpositive.python.command;

public interface IPythonPathProvider {
	
	PyInfo getPythonPath();

	public static class PyInfo{
		String pythonPath;
		public PyInfo(String pythonPath, String pythonInterpreter) {
			super();
			this.pythonPath = pythonPath;
			this.pythonInterpreter = pythonInterpreter;
		}
		String pythonInterpreter;
	}
}
