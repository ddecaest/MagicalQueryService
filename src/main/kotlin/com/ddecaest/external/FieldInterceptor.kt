package com.ddecaest.external

class FieldInterceptor<FIELD_TYPE>(val entityName: String, val fieldName: String, val transformField: (FIELD_TYPE) -> FIELD_TYPE)