package com.example.locationtask6.view;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndSingleStrategy;
import moxy.viewstate.strategy.StateStrategyType;

public interface LogInInterface extends MvpView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void makeToast(String toast);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void onSuccessAuth();
}
