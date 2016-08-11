package skritikou.taxibeattest.Views;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import skritikou.taxibeattest.Models.Venue;
import skritikou.taxibeattest.R;
import skritikou.taxibeattest.taxibeatUtilities.CommonUtils;


public class DetailedVenueFragment extends Fragment {

    @BindView(R.id.details_store_iv)
    ImageView mStore_iv;
    @BindView(R.id.details_storename_tv)
    TextView mStoreName_tv;
    @BindView(R.id.details_storerating_tv)
    TextView mStoreRating_tv;
    @BindView(R.id.details_storeaddress_tv)
    TextView mStoreAddress_tv;
    @BindView(R.id.details_storecategory_iv)
    TextView mStoreCategory_tv;
    @BindView(R.id.details_storedescr_tv)
    TextView mStoreDescr_tv;
    @BindView(R.id.details_storerating_wrapper)
    RelativeLayout mRatingWrapper_rl;
    private View mMainView;

    private MainActivity mActivity;
    private Context mContext;
    private Venue mCurrentVenue;
    private ActionBar mToolbar;


    public DetailedVenueFragment() {
        // Required empty public constructor
    }


    public static DetailedVenueFragment newInstance() {
        DetailedVenueFragment fragment = new DetailedVenueFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mActivity = (MainActivity)getActivity();
        mContext = mActivity.getApplicationContext();
        mToolbar = mActivity.getSupportActionBar();
        mToolbar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("Details");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
           mActivity.onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_close);
        item.setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_detailed_venue, container, false);
        return mMainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mCurrentVenue = mActivity.getmVenueSelected();
        CommonUtils utils = new CommonUtils();

        mStoreRating_tv.setTypeface(utils.getTypeface(2,mContext));
        mStoreName_tv.setTypeface(utils.getTypeface(2,mContext));
        mStoreAddress_tv.setTypeface(utils.getTypeface(0,mContext));
        mStoreCategory_tv.setTypeface(utils.getTypeface(0,mContext));
        mStoreDescr_tv.setTypeface(utils.getTypeface(0,mContext));

        final int radius = 20;
        final int margin = 0;
        final Transformation transformation = new RoundedCornersTransformation(radius, margin);

        Picasso.Builder builder = new Picasso.Builder(mContext);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.e("Picasso", "error in downloading");
                exception.printStackTrace();
            }
        });
        builder.build()
                .load(mCurrentVenue.getVenueImagePath())
                .placeholder(R.drawable.candyshopdefault)
                .transform(transformation)
                .resize(500,200)
                .centerCrop()
                .into(mStore_iv);

        mStoreName_tv.setText(mCurrentVenue.getName());
        mStoreAddress_tv.setText(mCurrentVenue.getAddress());
        if (mCurrentVenue.getRating().equals("-")){
            mRatingWrapper_rl.setVisibility(View.GONE);
        }else{
            mRatingWrapper_rl.setVisibility(View.VISIBLE);
            mStoreRating_tv.setText(mCurrentVenue.getRating());
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbar.setDisplayHomeAsUpEnabled(false);
        mToolbar.setTitle("CandyStore");
    }

}
