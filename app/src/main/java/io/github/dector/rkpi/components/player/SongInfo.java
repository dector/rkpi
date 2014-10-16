/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 dector
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
package io.github.dector.rkpi.components.player;

/**
 * Simple data structure to store and transfer song info
 *
 * @author dector
 */
public class SongInfo {

	/** Song performer */
	private String artist;
	/** Song name */
	private String title;

	/**
	 * Returns song artist
	 *
	 * @return song artist
	 */
	public String getArtist() {
		return artist;
	}

	public String getArtistOrEmpty() {
		return (artist != null) ? artist : "";
	}

	/**
	 * Sets song artist
	 *
	 * @param artist song artist
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * Returns song name
	 *
	 * @return song name
	 */
	public String getTitle() {
		return title;
	}

	public String getTitleOrEmpty() {
		return (title != null) ? title : "";
	}

	/**
	 * Sets song name
	 *
	 * @param title song name
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Return true if doesn't contains data about artist and title
	 *
	 * @return true if doesn't contains data about artist and title
	 */
	public boolean isEmpty() {
		return artist == null && title == null;
	}

	/**
	 * Returns string interpretation as <pre>Artist - Title</pre>
	 *
	 * @return string interpretation
	 */
	public String toString() {
		return artist + " - " + title;
	}
}
