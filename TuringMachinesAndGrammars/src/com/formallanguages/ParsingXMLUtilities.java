package com.formallanguages;

class ParsingXMLUtilities {
    private ParsingXMLUtilities(){}

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

    static Pair<Integer, String> parseStateOpenTag(String stateOpenTagLine) {//something like '<block id="2" name="q2">'
        String[] tagItems = stateOpenTagLine.trim().split(" ");
        assert tagItems[0].contains("block");

        String idInQuotes = tagItems[1].split("=")[1];
        int id = Integer.parseInt(idInQuotes.substring(1, idInQuotes.length() - 1));

        String nameInQuotes = tagItems[2].split("=")[1];
        String name = nameInQuotes.substring(nameInQuotes.indexOf("\"") + 1, nameInQuotes.lastIndexOf("\""));
        return new Pair<>(id, name);
    }
}
