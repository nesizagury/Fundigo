package com.example.FundigoApp.Customer.Social;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class MipoGridAdaptor extends BaseAdapter {
    private final List<MipoUser> list;
    private Context context;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    int pixels;
    boolean isFavorite;

    public MipoGridAdaptor(Context c, List<MipoUser> list, boolean isFavorite) {
        this.context = c;
        this.list = list;
        this.isFavorite = isFavorite;
        setImageLoader ();
    }

    @Override
    public int getCount() {
        return list.size ();
    }

    @Override
    public Object getItem(int i) {
        return list.get (i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final GridHolder gridHolder;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate (R.layout.mipo_grid_item, parent, false);
            gridHolder = new GridHolder (convertView);
            convertView.setTag (gridHolder);

        } else {
            gridHolder = (GridHolder) convertView.getTag ();
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams (pixels, pixels);
        lp.setMargins (0, 0, 0, 0);
        gridHolder.image.setLayoutParams (lp);

        final MipoUser user = list.get (position);
        if (user != null) {

            if (GlobalVariables.CUSTOMER_PHONE_NUM != null && user.getUserPhone ().equals (GlobalVariables.CUSTOMER_PHONE_NUM)) {
                lp.setMargins (5, 5, 5, 5);
                gridHolder.image.setLayoutParams (lp);
            }

            if (list.get (position).getPicUrl () != null) {
                imageLoader.displayImage (list.get (position).getPicUrl (), gridHolder.image);
            } else {
                gridHolder.image.setImageResource (R.drawable.anonymous);
            }
            gridHolder.name.setText (user.name);
        }
        return convertView;
    }

    class GridHolder {
        ImageView image;
        ImageView image2;
        TextView name;

        public GridHolder(View v) {
            image = (ImageView) v.findViewById (R.id.imageView1);
            image2 = (ImageView) v.findViewById (R.id.imageView2);
            name = (TextView) v.findViewById (R.id.textView1);
        }
    }

    public void setImageLoader() {
        float density = context.getResources ().getDisplayMetrics ().density;
        pixels = (int) (125 * density + 0.5f);

        options = new DisplayImageOptions.Builder ()
                          .cacheOnDisk (true)
                          .cacheInMemory (true)
                          .bitmapConfig (Bitmap.Config.RGB_565)
                          .imageScaleType (ImageScaleType.EXACTLY)
                          .resetViewBeforeLoading (true)
                          .build ();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder (context)
                                                  .defaultDisplayImageOptions (options)
                                                  .threadPriority (Thread.MAX_PRIORITY)
                                                  .threadPoolSize (4)
                                                  .memoryCache (new WeakMemoryCache ())
                                                  .denyCacheImageMultipleSizesInMemory ()
                                                  .build ();
        imageLoader = ImageLoader.getInstance ();
        imageLoader.init (config);
    }
}

