package com.onpositive.dside.ui.editors.preview;

/**
 * Completion suggestion
 * @author 32kda
 */
public class Suggestion {
	/**
	 * Text to insert into editor
	 */
	public final String text;
	/**
	 * Additional info - title
	 */
	public final String title;
	/**
	 * Additional info - description
	 */
	public final String description;
	
	/**
	 * Create Suggestion
	 * @param text Text to insert into editor
	 * @param title Additional info - title
	 * @param description Additional info - description
	 */
	public Suggestion(String text, String title, String description) {
		super();
		this.text = text;
		this.title = title;
		this.description = description;
	}
	
	/**
	 * Create Suggestion
	 * @param text Text to insert into editor
	 * @param description Additional info - description
	 */
	public Suggestion(String text, String description) {
		this(text, null, description);
	}
	
	/**
	 * Create Suggestion
	 * @param text Text to insert into editor
	 */
	public Suggestion(String text) {
		this(text, null);
	}

	@Override
	public String toString() {
		return "Suggestion [text=" + text + ", title=" + title + ", description=" + description + "]";
	}
}
