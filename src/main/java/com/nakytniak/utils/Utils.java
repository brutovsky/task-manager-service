package com.nakytniak.utils;

import com.nakytniak.entity.TaskType;

public class Utils {

    public static String formTemplateJobName(final TaskType type, final String school) {
        return type.name().toLowerCase() + "-" + school.toLowerCase() + "-job";
    }

}
