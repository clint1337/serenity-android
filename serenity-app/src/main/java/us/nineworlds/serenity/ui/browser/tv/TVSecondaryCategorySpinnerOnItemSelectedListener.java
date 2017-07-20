/**
 * The MIT License (MIT)
 * Copyright (c) 2012 David Carver
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package us.nineworlds.serenity.ui.browser.tv;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.bumptech.glide.request.animation.GlideAnimation;

import net.ganin.darv.DpadAwareRecyclerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.nineworlds.serenity.R;
import us.nineworlds.serenity.core.model.CategoryInfo;
import us.nineworlds.serenity.core.model.SecondaryCategoryInfo;
import us.nineworlds.serenity.injection.BaseInjector;

/**
 * Populate the tv show banners based on the information from the Secondary
 * categories.
 *
 * @author dcarver
 */
public class TVSecondaryCategorySpinnerOnItemSelectedListener extends
        BaseInjector implements OnItemSelectedListener {

    private String selected;
    private final String key;
    private boolean firstTimesw = true;

    @Inject
    SharedPreferences prefs;

    @Inject
    protected TVCategoryState categoryState;

    @BindView(R.id.tvShowGridView) @Nullable DpadAwareRecyclerView tvGridRecyclerView;
    @BindView(R.id.tvShowBannerGallery) @Nullable DpadAwareRecyclerView posterGallery;

    AdapterView<?> viewAdapter;


    public TVSecondaryCategorySpinnerOnItemSelectedListener(String defaultSelection, String key) {
        super();
        selected = defaultSelection;
        this.key = key;
    }

    @Override
    public void onItemSelected(AdapterView<?> viewAdapter, View view, int position, long id) {
        this.viewAdapter = viewAdapter;
        ButterKnife.bind(this, getActivity(viewAdapter.getContext()));

        SecondaryCategoryInfo item = (SecondaryCategoryInfo) viewAdapter.getItemAtPosition(position);

        boolean isGridViewActive = prefs.getBoolean("series_layout_grid", false);
        boolean posterLayoutActive = prefs.getBoolean("series_layout_posters", false);


        if (firstTimesw) {
            if (categoryState.getGenreCategory() != null) {
                int savedInstancePosition = getSavedInstancePosition(viewAdapter);
                item = (SecondaryCategoryInfo) viewAdapter
                        .getItemAtPosition(savedInstancePosition);
                viewAdapter.setSelection(savedInstancePosition);
            }
            firstTimesw = false;
        }

        if (selected.equalsIgnoreCase(item.getCategory())) {
            return;
        }

        selected = item.getCategory();
        categoryState.setGenreCategory(item.getCategory());

        if (isGridViewActive) {
            tvGridRecyclerView.setAdapter(new TVShowPosterImageGalleryAdapter());
            tvGridRecyclerView.setOnItemSelectedListener(new TVShowGridOnItemSelectedListener());
            refreshShows(key, item.getParentCategory() + "/" + item.getCategory());
            return;
        }

        if (posterLayoutActive) {
            posterGallery.setAdapter(new TVShowPosterImageGalleryAdapter());
        } else {
            posterGallery.setAdapter(new TVShowRecyclerAdapter());
        }

        refreshShows(key, item.getParentCategory() + "/" + item.getCategory());

        posterGallery.setOnItemSelectedListener(new TVShowGalleryOnItemSelectedListener());
//			posterGallery
//					.setOnItemLongClickListener(new ShowOnItemLongClickListener());
    }

    private void refreshShows(String key, String category) {
        Activity activity = getActivity(viewAdapter.getContext());
        if (activity instanceof TVShowBrowserActivity) {
            TVShowBrowserActivity a = (TVShowBrowserActivity) activity;
            a.requestUpdatedVideos(key, category);
        }
    }


    private int getSavedInstancePosition(AdapterView<?> viewAdapter) {
        int count = viewAdapter.getCount();
        for (int i = 0; i < count; i++) {
            CategoryInfo citem = (CategoryInfo) viewAdapter
                    .getItemAtPosition(i);
            if (citem.getCategory().equals(categoryState.getGenreCategory())) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onNothingSelected(AdapterView<?> va) {

    }

}
