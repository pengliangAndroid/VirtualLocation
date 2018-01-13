package com.wstro.virtuallocation.delegate;

import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.remote.InstallResult;
import com.wstro.app.common.utils.LogUtil;

import java.io.IOException;

/**
 * @author Lody
 */

public class MyAppRequestListener implements VirtualCore.AppRequestListener {

    //private final Context context;

    public MyAppRequestListener() {
        //this.context = context;
    }

    @Override
    public void onRequestInstall(String path) {
        //Toast.makeText(context, "Installing: " + path, Toast.LENGTH_SHORT).show();
        LogUtil.d("Installing: " + path);

        InstallResult res = VirtualCore.get().installPackage(path, InstallStrategy.UPDATE_IF_EXIST);
        if (res.isSuccess) {
            try {
                VirtualCore.get().preOpt(res.packageName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (res.isUpdate) {
                //RxBus.getDefault().post(new InstallAppStatusEvent(res.packageName,InstallAppStatus.UPDATE_SUCCESS));
                LogUtil.d("Update: " + res.packageName + " success!");
                //Toast.makeText(context, "Update: " + res.packageName + " success!", Toast.LENGTH_SHORT).show();
            } else {
                LogUtil.d("Install: " + res.packageName + " success!");
                //RxBus.getDefault().post(new InstallAppStatusEvent(res.packageName,InstallAppStatus.INSTALL_SUCCESS));
                //Toast.makeText(context, "Install: " + res.packageName + " success!", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Toast.makeText(context, "Install failed: " + res.error, Toast.LENGTH_SHORT).show();
            LogUtil.d("Install failed: " + res.error);
            //RxBus.getDefault().post(new InstallAppStatusEvent(res.packageName,InstallAppStatus.INSTALL_FAIL));
        }
    }

    @Override
    public void onRequestUninstall(String pkg) {
        //Toast.makeText(context, "Uninstall: " + pkg, Toast.LENGTH_SHORT).show();
        LogUtil.d("Uninstall: " + pkg);
        //RxBus.getDefault().post(new InstallAppStatusEvent(pkg,InstallAppStatus.UNINSTALL_SUCCESS));
    }
}
