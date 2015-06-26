# WhatsUp(Cus!News)

App supports English, Chinese, German content.

App supports push-notifications with buildIn topics(updated pro App-Update).

App supports customizing different push-topics.

App supports saving bookmarks in cloud.

App supports three view-modes and compatible with tablet.

App supports non-image-mode.

App based on Google GAE.

App based on [bmob](http://www.bmob.cn).

App based [Faroo API](http://www.faroo.com/hp/api/api.html#description).

Related project [faroo-push](https://github.com/XinyueZ/faroo-push) which provides push-functions for client including WhatsUp.

######Released.
[![https://play.google.com/store/apps/details?id=com.cusnews](https://dl.dropbox.com/s/phrg0387osr3riz/images.jpeg)](https://play.google.com/store/apps/details?id=com.cusnews)


# Tech
Use [Faroo API](http://www.faroo.com/hp/api/api.html#description) to fetch news feeds.

Also to demonstrate new Google's [design-library](http://developer.android.com/intl/zh-cn/tools/support-library/features.html#design). But I discard its own [FAB](http://developer.android.com/intl/zh-cn/reference/android/support/design/widget/FloatingActionButton.html) instead by [fab project](https://github.com/shell-software/fab) .

New [data-binding](https://developer.android.com/intl/zh-cn/tools/data-binding/guide.html) infrastruct will also be included.

New GCM features like group, topics will be built to subscribe different daily news update.

Cloud on [bmob](http://www.bmob.cn)

The help of dependence of [jsoup](http://www.jsoup.org) to filter out html-tags.

# API
Enter lot keys in key.properties under resources directory(live, dev flavors).
Don't publish keys to public when fork the project to avoid abuse indeed.

An example of key.properties under resources.

```java
appkey=YciwC32TOr
bmobkey=25e18a1ae36
senderId=5345345345
````
1. appkey: The api-key from [faroo.com](http://www.faroo.com).
2. bmobkey: The application-id from [bmob.com](http://www.bmob.cn).
3. senderId: The push-backend on [GAE](http://developer.google.com).

Notices:

The backend-db is [bmob](http://www.bmob.cn).

The push-backend based on Google GAE, programmed by [golang](http://www.golang.org).


```java
				The MIT License (MIT)

			Copyright (c) 2015 Chris Xinyue Zhao

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
```
