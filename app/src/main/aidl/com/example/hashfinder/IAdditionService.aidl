// IAdditionService.aidl
package com.example.hashfinder;

import com.example.hashfinder.Hash;

// Declare any non-default types here with import statements

interface IAdditionService {
    int add(in Hash hash, in String strHash);
}