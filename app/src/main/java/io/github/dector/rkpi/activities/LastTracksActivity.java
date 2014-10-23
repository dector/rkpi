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
package io.github.dector.rkpi.activities;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.dector.rkpi.R;
import io.github.dector.rkpi.lastfm.LastFmAPI;
import io.github.dector.rkpi.lastfm.Track;

/**
 * @author dector
 */
public class LastTracksActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FIXME use loaders and fragments
        new AsyncTask<Void, Void, List<Track>>() {
            @Override
            protected List<Track> doInBackground(Void... params) {
                return new LastFmAPI().getTracks();
            }

            @Override
            protected void onPostExecute(List<Track> tracks) {
                setListAdapter(new TracksListAdapter(LastTracksActivity.this, tracks));
            }
        }.execute();
    }

    private static class TracksListAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private List<Track> tracks;

        private TracksListAdapter(Context ctx, List<Track> tracks) {
            this.tracks = tracks;

            this.inflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getCount() {
            return tracks.size();
        }

        @Override
        public Object getItem(int position) {
            return tracks.get(position);
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
    }
}
