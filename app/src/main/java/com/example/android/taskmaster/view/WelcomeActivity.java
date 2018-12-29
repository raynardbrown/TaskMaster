package com.example.android.taskmaster.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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

      startActivity(intent);
    }
  }

  class SignUpButtonClickHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      Intent intent = SignUpActivity.getStartIntent(WelcomeActivity.this);

      startActivity(intent);
    }
  }
}
