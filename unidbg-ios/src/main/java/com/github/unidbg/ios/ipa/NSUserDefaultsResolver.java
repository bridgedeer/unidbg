package com.github.unidbg.ios.ipa;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.github.unidbg.Emulator;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.file.ios.DarwinFileIO;
import com.github.unidbg.ios.file.ByteArrayFileIO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class NSUserDefaultsResolver implements IOResolver<DarwinFileIO> {

    private final String bundleIdentifier;
    private final Map<String, Object> map;

    @SuppressWarnings("unused")
    public NSUserDefaultsResolver(String bundleIdentifier) {
        this(bundleIdentifier, Collections.<String, Object>emptyMap());
    }

    public NSUserDefaultsResolver(String bundleIdentifier, Map<String, Object> map) {
        this.bundleIdentifier = bundleIdentifier;
        this.map = map;
    }

    @Override
    public FileResult<DarwinFileIO> resolve(Emulator<DarwinFileIO> emulator, String pathname, int oflags) {
        if (("/var/root/Library/Preferences/" + bundleIdentifier + ".plist").equals(pathname)) {
            NSDictionary root = (NSDictionary) NSDictionary.fromJavaObject(map);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                PropertyListParser.saveAsBinary(root, outputStream);
            } catch (IOException e) {
                throw new IllegalStateException("save plist failed", e);
            }
            return FileResult.<DarwinFileIO>fallback(new ByteArrayFileIO(oflags, pathname, outputStream.toByteArray()));
        }
        return null;
    }

}
