

package org.zlibrary.core.html;

public interface ZLHtmlReader {
	public void startDocumentHandler();
	public void endDocumentHandler();

	public void startElementHandler(String tag, int offset, ZLHtmlAttributeMap attributes);
	public void endElementHandler(String tag);
	public void byteDataHandler(byte[] ch, int start, int length);
	public void entityDataHandler(String entity);
}
