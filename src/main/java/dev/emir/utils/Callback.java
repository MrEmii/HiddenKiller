package dev.emir.utils;

import java.util.ArrayList;

public interface Callback<T> {
    ArrayList<T> replaceList(ArrayList<String> strings);
}
