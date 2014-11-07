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
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import io.github.dector.rkpi.lastfm.LastFmAPI;
import io.github.dector.rkpi.lastfm.Track;

/**
* @author dector
*/
class LastTracksLoader extends AsyncTaskLoader<List<Track>> {

    private List<Track> data;

    LastTracksLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (data == null || takeContentChanged()) {
            forceLoad();
        } else {
            deliverResult(data);
        }
    }

    @Override
    public List<Track> loadInBackground() {
        return new LastFmAPI().getTracks();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void deliverResult(List<Track> data) {
        this.data = data;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        data = null;
        stopLoading();
    }
}
