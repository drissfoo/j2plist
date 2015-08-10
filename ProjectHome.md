j2plist is a simple property list (plist) converter library. Property lists are frequently used by applications running on Mac OS X or iPhone OS.
[More information about plist](http://developer.apple.com/mac/library/documentation/cocoa/conceptual/propertylists/Introduction/Introduction.html)
## Simple Usage ##

**Java code:**
```
 PList plist = new PList();
 Map map = new HashMap();
 map.put("name", "John Doe");
 map.put("age", 25);
 map.put("single", true);

 try {
    System.out.println(plist.encode(map));
 } catch (Exception e) {
    e.printStackTrace();
 }
```

**Result:**
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
	<dict>
	<key>single</key>
	<true/>
	<key>age</key>
	<integer>25</integer>
	<key>name</key>
	<string>John Doe</string>
	</dict>
</plist>
```

  * [More Example](Usage.md)
  * [Type Mapping](TypeMapping.md)