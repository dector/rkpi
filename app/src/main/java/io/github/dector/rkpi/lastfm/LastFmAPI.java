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
package io.github.dector.rkpi.lastfm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.minidev.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.dector.rkpi.BuildConfig;

/**
 * @author dector
 */
public class LastFmAPI {

    private static final String API_URI = "http://ws.audioscrobbler.com/2.0/?method=user.getRecentTracks&user=radiokpi&format=json&api_key="
            + BuildConfig.LASTFM_API_KEY;

    public List<Track> getTracks() {
        final List<Track> result = new ArrayList<Track>();

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(API_URI)
                .build();

        try {
            final Response response = client.newCall(request).execute();
            final String responseJson = response.body().string();

            final String tracksJson = JsonPath.read(responseJson, "$.recenttracks.track[*]").toString();

            final ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            final JsonNode root = mapper.readTree(tracksJson);
            final Iterator<JsonNode> trackIterator = root.iterator();
            while (trackIterator.hasNext()) {
                final JsonNode trackNode = trackIterator.next();
                final Track track = new Track();

                track.setArtist(trackNode.get("artist").get("#text").asText());
                track.setName(trackNode.get("name").asText());

                result.add(track);
            }
        } catch (IOException e) {
        }

        return result;
    }
}
