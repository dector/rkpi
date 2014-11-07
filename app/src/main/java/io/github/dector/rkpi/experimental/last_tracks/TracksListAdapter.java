/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 dector
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.dector.rkpi.experimental.last_tracks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.dector.rkpi.R;
import io.github.dector.rkpi.lastfm.Track;

/**
* @author dector
*/
class TracksListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Track> tracks;

    TracksListAdapter(Context ctx) {
        this(ctx, null);
    }

    TracksListAdapter(Context ctx, List<Track> tracks) {
        this.tracks = tracks;

        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return tracks != null ? tracks.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return tracks != null ? tracks.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_last_tracks, parent, false);
        }

        final Track track = (Track) getItem(position);
        ((TextView) convertView).setText(track.getArtist() + " - " + track.getName());

        return convertView;
    }

    public void setData(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }
}
