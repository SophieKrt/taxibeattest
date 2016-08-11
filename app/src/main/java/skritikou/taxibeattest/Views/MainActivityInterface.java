package skritikou.taxibeattest.Views;

import android.app.Fragment;

import skritikou.taxibeattest.Models.Venue;
import skritikou.taxibeattest.Presenters.MapPresenterImpl;

public interface MainActivityInterface {
    void addNewFragment(Fragment fragment, String tag);

    MapPresenterImpl getMapPresenter();

    Venue getmVenueSelected();

    void setmVenueSelected(Venue mVenueSelected);

    void startMap();

    void showPromptForGps();

    void removeGpsDialog();

    void showMessageForRequestingPerms(String caseofPerms, String prompt);
}
