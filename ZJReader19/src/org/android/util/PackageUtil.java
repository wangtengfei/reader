package org.android.util;

import java.util.Map;

import org.zlibrary.core.options.ZLBooleanOption;
import org.zlibrary.core.resources.ZLResource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.rdweiba.main.R;



public abstract class PackageUtil {
	private static Uri marketUri(String pkg) {
		return Uri.parse("market://details?id=" + pkg);
	}

	private static Uri homeUri(String pkg) {
		return null; // Uri.parse("http://data.fbreader.org/android/packages/" + pkg + ".apk");
	}

	private static Uri homeUri(String pkg, String version) {
		return null; // Uri.parse("http://data.fbreader.org/android/packages/" + pkg + ".apk_version_" + version);
	}

	public static boolean isPluginInstalled(Activity activity, String pkg) {
		return canBeStarted(
			activity,
			new Intent("android.zjreader.action.TEST", homeUri(pkg)),
			true
		);
	}

	public static boolean isPluginInstalled(Activity activity, String pkg, String version) {
		return canBeStarted(
			activity,
			new Intent("android.zjreader.action.TEST", homeUri(pkg, version)),
			true
		);
	}

	public static boolean canBeStarted(Context context, Intent intent, boolean checkSignature) {
		final PackageManager manager = context.getApplicationContext().getPackageManager();
		final ResolveInfo info =
			manager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (info == null) {
			return false;
		}
		final ActivityInfo activityInfo = info.activityInfo;
		if (activityInfo == null) {
			return false;
		}
		if (!checkSignature) {
			return true;
		}
		return
			PackageManager.SIGNATURE_MATCH ==
			manager.checkSignatures(context.getPackageName(), activityInfo.packageName);
	}

	public static boolean installFromMarket(Activity activity, String pkg) {
		try {
			activity.startActivity(new Intent(
				Intent.ACTION_VIEW, marketUri(pkg)
			));
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}

	public static void runInstallPluginDialog(final Activity activity, Map<String,String> pluginData, final Runnable postRunnable) {
		String pluginName = pluginData.get("androidPlugin");
		if (pluginName == null) {
			final TelephonyManager telephony =
				(TelephonyManager)activity.getSystemService(Activity.TELEPHONY_SERVICE);
			if (telephony != null &&
				(telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM ||
				 telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA)) {
				pluginName = pluginData.get("androidPlugin:gsm");
			} else {
				pluginName = pluginData.get("androidPlugin:no_gsm");
			}
		}
		final String plugin = pluginName;
		if (plugin != null) {
			final ZLBooleanOption doNotInstallOption = new ZLBooleanOption("doNotInstall", plugin, false);
			if (!doNotInstallOption.getValue()) {
				final String pluginVersion = pluginData.get("androidPluginVersion");
            
				String message = null;
				String positiveButtonKey = null;
				String titleResourceKey = null;
				
				if (!PackageUtil.isPluginInstalled(activity, plugin)) {
					message = pluginData.get("androidPluginInstallMessage");
					positiveButtonKey = "install";
					titleResourceKey = "installTitle";
				} else if (!PackageUtil.isPluginInstalled(activity, plugin, pluginVersion)) {
					message = pluginData.get("androidPluginUpdateMessage");
					positiveButtonKey = "update";
					titleResourceKey = "updateTitle";
				}
				if (message != null) {
					final ZLResource dialogResource = ZLResource.resource("dialog");
					final ZLResource pluginDialogResource = dialogResource.getResource("plugin");
					final ZLResource buttonResource = dialogResource.getResource("button");
					final View view = activity.getLayoutInflater().inflate(R.layout.plugin_dialog, null, false);
					((TextView)view.findViewById(R.id.plugin_dialog_text)).setText(message);
					final CheckBox checkBox = (CheckBox)view.findViewById(R.id.plugin_dialog_checkbox);
					checkBox.setText(pluginDialogResource.getResource("dontAskAgain").getValue());
					final AlertDialog dialog = new AlertDialog.Builder(activity)
						.setTitle(pluginDialogResource.getResource(titleResourceKey).getValue())
						.setView(view)
						.setIcon(0)
						.setPositiveButton(
							buttonResource.getResource(positiveButtonKey).getValue(),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									if (!PackageUtil.installFromMarket(activity, plugin)) {
										UIUtil.showErrorMessage(activity, "cannotRunAndroidMarket", "plugin");
									}
								}
							}
						)
						.setNegativeButton(
							buttonResource.getResource("skip").getValue(),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									doNotInstallOption.setValue(checkBox.isChecked());
									postRunnable.run();
								}
							}
						)
						.create();
					dialog.show();
					return;
				}
			}
		}
		postRunnable.run();
	}
}
