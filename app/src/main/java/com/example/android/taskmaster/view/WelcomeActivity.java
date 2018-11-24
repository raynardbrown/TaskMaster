package com.example.android.taskmaster.view;

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
      Intent intent = new Intent(WelcomeActivity.this, LogInActivity.class);

      startActivity(intent);
    }
  }

  class SignUpButtonClickHandler implements View.OnClickListener
  {

    @Override
    public void onClick(View v)
    {
      Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);

      startActivity(intent);
    }
  }
}
