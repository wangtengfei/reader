/*
 * This code is in the public domain.
 */

package org.android.zjreader.api;

import org.android.zjreader.api.ApiObject;

interface ApiInterface {
	ApiObject request(int method, in ApiObject[] parameters);
	List<ApiObject> requestList(int method, in ApiObject[] parameters);
	Map requestMap(int method, in ApiObject[] parameters);
}
