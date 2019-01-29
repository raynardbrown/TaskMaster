package com.example.android.taskmaster.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class NetworkBroadcastReceiver extends BroadcastReceiver
{
  private INetworkEventHandler networkEventHandler;

  public NetworkBroadcastReceiver(INetworkEventHandler networkEventHandler)
  {
    this.networkEventHandler = networkEventHandler;
  }

  @Override
  public void onReceive(Context context, Intent intent)
  {
    String action = intent.getAction();

    if(action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
    {
      if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false))
      {
        // We have no connectivity
        networkEventHandler.onNetworkInactive();
      }
      else
      {
        // We have connectivity
        networkEventHandler.onNetworkActive();
      }
    }
  }
}
