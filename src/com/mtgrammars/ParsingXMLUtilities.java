package com.mtgrammars;

/**
 * Created by Alex on 02.10.2016.
 */

class ParsingXMLUtilities {
    //Works for lines like "<to>5</to>"
    static String getXmlElementsContentSingleLine(String line) {
        int readBegin = line.indexOf('>') + 1;
        int readEnd = line.lastIndexOf('<');
        if (readBegin <= readEnd) return line.substring(readBegin, readEnd);
        return null;
    }

    //Works for lines like "</smth>"
    static String parseEmptyClosingTag(String tag) { // 'empty' means smth like "</dec.jff>"
        return tag.trim().substring(2, tag.length() - 1);
    }
}
