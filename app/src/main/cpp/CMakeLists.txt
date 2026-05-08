cmake_minimum_required(VERSION 3.22.1)

project("jni_bridge_lab")

add_library(
        secure_bridge
        SHARED
        native-lib.cpp
)

find_library(
        android_log
        log
)

target_link_libraries(
        secure_bridge
        ${android_log}
)