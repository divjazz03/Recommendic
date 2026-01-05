package com.divjazz.recommendic.global.validation;

import com.divjazz.recommendic.global.validation.annotation.ValidEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;
public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;
    private Method fromValueMethod;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
        this.fromValueMethod = findFromValueMethod(enumClass);
    }

    private Method findFromValueMethod(Class<? extends Enum<?>> enumClass) {
        try {
            Method method = enumClass.getDeclaredMethod("fromValue", String.class);

            if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())
                    || !enumClass.isAssignableFrom(method.getReturnType())) {
                return null;
            }
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        var result = false;
        if (fromValueMethod != null) {
            try {
                fromValueMethod.invoke(null, s);
                result = true;
            } catch (Exception ignored) {
            }
        }

        try{
            Enum.valueOf(enumClass.asSubclass(Enum.class), s.toUpperCase());
            result = true;
        } catch (IllegalArgumentException ignored) {
        }

        return result;
    }
}
