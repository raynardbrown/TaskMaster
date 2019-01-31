package com.example.android.taskmaster.view;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity
{
  private ActivityWelcomeBinding binding;

  public static Intent getStartIntent(Context context)
  {
    Intent intent = new Intent(context, WelcomeActivity.class);

    // Also clear the back stack
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
            Intent.FLAG_ACTIVITY_CLEAR_TASK  |
            Intent.FLAG_ACTIVITY_NEW_TASK);

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    // must be called before set content view
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    {
      getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

      // set a slide exit transition
      getWindow().setExitTransition(new Slide(Gravity.END));
    }

    binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);

    registerClickHandlers();
  }

  private void registerClickHandlers()
  {
    binding.buttonWelcomeActivityLogIn.setOnClickListener(new LogInButtonClickHandler());
    binding.buttonWelcomeActivitySignUp.setOnClickListener(new SignUpButtonClickHandler());
  }

  class LogInButtonClickHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      Intent intent = LogInActivity.getStartIntent(WelcomeActivity.this);

      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
      {
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this).toBundle());
      }
      else
      {
        // standard transition on older Android versions
        startActivity(intent);
      }
    }
  }

  class SignUpButtonClickHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      Intent intent = SignUpActivity.getStartIntent(WelcomeActivity.this);

      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
      {
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this).toBundle());
      }
      else
      {
        // standard transition on older Android versions
        startActivity(intent);
      }
    }
  }
}
