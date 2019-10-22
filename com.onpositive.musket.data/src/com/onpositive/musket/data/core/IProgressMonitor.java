package com.onpositive.musket.data.core;

public interface IProgressMonitor {

	public boolean onBegin(String message,int totalTicks);
	public boolean onProgress(String message,int passsedTicks);
	public boolean onDone(String message,int totalTicks);
	
	public static IProgressMonitor nullProgressMonitor() {
		return new NullProgressMonitor();
	}

	public static class NullProgressMonitor implements IProgressMonitor{

		@Override
		public boolean onBegin(String message, int totalTicks) {
			return true;
		}

		@Override
		public boolean onProgress(String message, int passsedTicks) {
			return true;
		}

		@Override
		public boolean onDone(String message, int totalTicks) {
			return true;
		}
		
	}
}
