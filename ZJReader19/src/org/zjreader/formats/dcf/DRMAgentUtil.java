/**
 * 
 */
package org.zjreader.formats.dcf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.rdweiba.main.Const;
import com.sansec.DRMAgent.DRMAgent;

/**
 * @author Administrator
 * 
 */
public class DRMAgentUtil
{

	public static String g_sDRMLogTag = "DRMLOG";
	private int m_nRet;
	String m_sCoFileName = new String();
	String m_sRoFileName = new String();
	String m_sOutFileName = new String();

	private void log(String sLog)
	{
		Log.i(g_sDRMLogTag, sLog);
	}

	private static DRMAgentUtil instance = null;

	public static DRMAgentUtil getInstance()
	{
		return instance == null ? new DRMAgentUtil() : instance;
	}

	// 生成明文文件
	public String decryptFile(String co, String ro, String cert, String key, String type) throws Exception
	{
		if (ro == null || co == null)
		{
			return null;
		}
		DRMAgent agent = new DRMAgent(cert, key);
		Date dStart = Calendar.getInstance().getTime();
		m_nRet = agent.DRM_VerifyPIN("123456".getBytes());
		log("TestGetROInfo DRM_VerifyPIN return : " + m_nRet);
		Date dEnd = Calendar.getInstance().getTime();
		log("DRM_VerifyPIN Use time is : " + Long.toString(dEnd.getTime() - dStart.getTime()));
		log("m_nRet : " + m_nRet);
		m_sCoFileName = new String(co);
		m_sRoFileName = new String(ro);
		log("m_sRoFileName : " + m_sRoFileName);
		log("m_sCoFileName : " + m_sCoFileName);
		GetDeviceCertID(agent);
		DecryptedWholeFile(agent, type);
		// ReadFile(agent, type);
		File file = new File(m_sOutFileName);
		return file.getAbsolutePath();
	}

	// 生成明文流
	public InputStream decryptFileToStream(String co, String ro, String cert, String key) throws Exception
	{
		if (ro == null || co == null)
		{
			return null;
		}
		DRMAgent agent = new DRMAgent(cert, key);
		Date dStart = Calendar.getInstance().getTime();
		m_nRet = agent.DRM_VerifyPIN("123456".getBytes());
		log("TestGetROInfo DRM_VerifyPIN return : " + m_nRet);
		Date dEnd = Calendar.getInstance().getTime();
		log("DRM_VerifyPIN Use time is : " + Long.toString(dEnd.getTime() - dStart.getTime()));
		log("m_nRet : " + m_nRet);
		m_sCoFileName = new String(co);
		m_sRoFileName = new String(ro);
		log("m_sRoFileName : " + m_sRoFileName);
		log("m_sCoFileName : " + m_sCoFileName);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int nBufferSize = 1024;
		byte[] buffer = new byte[nBufferSize];
		int offset = 0;
		int rv = 0;
		rv = agent.DRM_OpenFile(m_sCoFileName, m_sRoFileName);
		while (true)
		{
			rv = agent.DRM_ReadFile(buffer, nBufferSize, offset);
			if (rv > 0)
			{
				out.write(buffer, 0, rv);
				offset += rv;
			} else
			{
				log("DRM_ReadFile return " + rv);
				break;
			}
		}
		rv = agent.DRM_CloseFile();
		return new ByteArrayInputStream(out.toByteArray());
	}

	public int DecryptedWholeFile(DRMAgent agent, String type) throws Exception
	{
		System.out.println("Const.filePath" + Const.filePath);
		if (!Const.filePath.equals(""))
		{
			m_sOutFileName = new String(Const.filePath + File.separator + getBASE64(m_sCoFileName.substring(m_sCoFileName.lastIndexOf("/"), m_sCoFileName.length())).toString()
					+ ".tmp");
		} else
		{
			m_sOutFileName = new String(m_sCoFileName + ".tmp");
		}
		System.out.println("m_sCoFileName:" + m_sCoFileName);
		System.out.println("m_sRoFileName:" + m_sRoFileName);
		System.out.println("m_sOutFileName:" + m_sOutFileName);
		m_nRet = agent.DRM_DecryptWholeFile(m_sCoFileName, m_sRoFileName, m_sOutFileName);
		System.out.println("m_nRet111111111" + m_nRet);
		return m_nRet;
	}

	public int ReadFile(DRMAgent agent, String type) throws Exception
	{
		String m_sOutFileName = new String(m_sCoFileName + type);
		File file = new File(m_sOutFileName);
		if (!file.exists())
		{
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file);

		int nBufferSize = 1024 * 4;
		byte[] buffer = new byte[nBufferSize];
		int offset = 0;
		int rv = 0;

		rv = agent.DRM_OpenFile(m_sCoFileName, m_sRoFileName);
		if (rv != 0)
		{
			log("DRM_OpenFile return " + rv);
			return rv;
		}

		while (true)
		{
			rv = agent.DRM_ReadFile(buffer, nBufferSize, offset);
			if (rv > 0)
			{
				// write file
				// out.write(buffer, 0, rv);
				offset += rv;
			} else
			{
				log("DRM_ReadFile return " + rv);
				break;
			}
		}

		rv = agent.DRM_CloseFile();
		if (rv != 0)
		{
			log("DRM_CloseFile return " + rv);
			return rv;
		}

		return m_nRet;
	}

	public int GetDeviceCertID(DRMAgent agent) throws Exception
	{
		String test = agent.DRM_GetCertID();
		log("[DRMAgentTest]DRM_GetCertID return : " + test);
		if (test == null)
		{
			log("[DRMAgentTest]DRM_GetCertID return null errno : " + agent.DRM_GetLastErrno());
		}
		return m_nRet;
	}

	public static String getBASE64(String s)
	{
		String aaa = Base64.encodeToString(s.getBytes(), Base64.DEFAULT).toLowerCase().replace("\n", "");
		System.out.println(aaa);
		return aaa;
		// return s;
	}
}
