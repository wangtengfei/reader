

package org.zjreader.bookmodel;


import org.zlibrary.core.tree.ZLTree;
import org.zlibrary.text.model.ZLTextModel;

public class TOCTree extends ZLTree<TOCTree> {
	private String myText;
	private Reference myReference;

	protected TOCTree() {
		super();
	}

	public TOCTree(TOCTree parent) {
		super(parent);
	}

	public final String getText() {
		return myText;
	}

	public final void setText(String text) {
		myText = text;
	}
	
	public Reference getReference() {
		return myReference;
	}
	
	public void setReference(ZLTextModel model, int reference) {
		myReference = new Reference(reference, model);
	}

	public static class Reference {
		public final int ParagraphIndex;
		public final ZLTextModel Model;
		
		public Reference(final int paragraphIndex, final ZLTextModel model) {
			ParagraphIndex = paragraphIndex;
			Model = model;
		}
	}
}
