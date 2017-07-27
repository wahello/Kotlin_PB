package com.megoapp.features.tos.presentation;


import com.megoapp.features.tos.usecases.LoadTos;

import dagger.Module;
import dagger.Provides;

@Module
public class TosModule {

    private final TosContract.View view;

    public TosModule(TosContract.View view) {
        this.view = view;
    }

    @Provides
    public TosContract.View provideView() {
        return this.view;
    }

    @Provides
    public TosContract.Presenter providePresenter(TosPresenter presenter) {
        return presenter;
    }

}
