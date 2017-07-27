package com.megoapp.views.activities;

import android.view.MenuItem;

import com.megoapp.R;
import com.megoapp.features.events.OpenViewEvent;
import com.megoapp.features.events.ViewAppBarEvent;
import com.megoapp.utilities.StringUtils;
import com.megoapp.utilities.navigation.AppNavigator;
import com.megoapp.utilities.rx.RxDisposableFactory;
import com.megoapp.utilities.rx.RxDisposables;
import com.megoapp.utilities.rx.ThreadTransformer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import static com.megoapp.utilities.navigation.AppNavigator.ViewTag.MAP_VIEW;
import static com.megoapp.utilities.navigation.AppNavigator.ViewTag.NAVIGATION_DRAWER;

public final class MeGoActivityPresenter implements MeGoActivityContract.Presenter {

    private final MeGoActivityContract.View view;
    private final EventBus bus;
    private final StringUtils stringUtils;
    private final AppNavigator navigator;
    private final ThreadTransformer threadTransformer;
    private final RxDisposableFactory rxDisposableFactory;
    private final RxDisposables disposables;

    @Inject
    public MeGoActivityPresenter(MeGoActivityContract.View view,
                                 EventBus bus,
                                 StringUtils stringUtils,
                                 AppNavigator navigator,
                                 ThreadTransformer threadTransformer,
                                 RxDisposableFactory rxDisposableFactory) {
        this.view = view;
        this.bus = bus;
        this.stringUtils = stringUtils;
        this.navigator = navigator;
        this.threadTransformer = threadTransformer;
        this.disposables = rxDisposableFactory.get();
        this.rxDisposableFactory = rxDisposableFactory;
    }

    @Subscribe
    public void handle(OpenViewEvent event) {
        switch (event.viewTag) {
            case VEHICLE_LIST:
                view.goToVehicleList();
                view.showAppToolbar();
                view.changeMenuIconToCross();
                break;
        }
    }

    @Subscribe
    public void handle(ViewAppBarEvent event) {
        view.openDrawer();
    }

    @Override
    public void onStart() {
        if (navigator.getViewStackSize() == 0) {
            view.addMapView();
            view.hideAppToolbar();
        }

        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }

    @Override
    public void onToolbarMenuTapped() {
        switch (navigator.getViewStackSize()) {
            case 2:
                view.goBack();
                view.hideAppToolbar();
                break;
        }
    }

    @Override
    public boolean onDrawerItemTapped(MenuItem drawerItem) {
        view.closeDrawer();
        view.lockDrawer();
        view.showAppToolbar();

        switch (drawerItem.getItemId()) {
            case R.id.trips:
                view.goToTrips();
                return true;
            case R.id.providers:
                view.goToProviders();
                return true;
            case R.id.questions:
                view.goToQuestions();
                return true;
            case R.id.about:
                view.goToAbout();
                return true;
            case R.id.legal:
                view.goToLegal();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onBackTapped() {
        if (MAP_VIEW.equals(navigator.getTopView())) {
            view.quitApp();
        } else if (NAVIGATION_DRAWER.equals(navigator.getTopView())) {
            view.closeDrawer();
            view.unlockDrawer();
        } else {
            view.hideAppToolbar();
            view.unlockDrawer();
            view.invokeSystemBack();
        }
    }

    @Override
    public void onDrawerOpened() {
        navigator.addDrawer();
    }

    @Override
    public void onDrawerClosed() {
        navigator.removeDrawer();
        if (MAP_VIEW.equals(navigator.getTopView())) {
            view.hideAppToolbar();
        } else {
            view.showAppToolbar();
        }
    }
}
