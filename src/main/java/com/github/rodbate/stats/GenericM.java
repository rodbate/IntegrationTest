package com.github.rodbate.stats;


import io.netty.util.internal.ConcurrentSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class GenericM {


    public static void main(String[] args) {


        TypeVariable<Class<Generic>>[] typeParameters = Generic.class.getTypeParameters();

        /*for (int i = 0; i < typeParameters.length; i++) {
            TypeVariable<Class<Generic>> param = typeParameters[i];
            System.out.println(param.getName());

            System.out.println(param instanceof ParameterizedType);

            System.out.println(param.getBounds().length);
            System.out.println(param.getBounds()[0].getTypeName());
            System.out.println(param.getGenericDeclaration().getName());
        }*/

        Generic<Integer, String> g = new Generic<>();


        TypeVariable<? extends Class<? extends Generic>>[] parameters = (new Generic<Integer, String>(){}.getClass()).getTypeParameters();


        Type genericSuperclass = (new Generic<Integer, String>(){}.getClass()).getGenericSuperclass();

        System.out.println(genericSuperclass.getTypeName());
        System.out.println(genericSuperclass instanceof ParameterizedType);


        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) genericSuperclass;
            Type[] actualTypeArguments = type.getActualTypeArguments();
            System.out.println("raw type : " + type.getRawType());


            for (int i = 0; i < actualTypeArguments.length; i++) {
                System.out.println(actualTypeArguments[i] instanceof Class);
                System.out.println(actualTypeArguments[i].getTypeName());
            }
        }


        Collection<Super> coll = new HashSet<>();
        Collection<Sub> col = new HashSet<>();

        t(coll);

        t(col);

        t(coll);

        te(coll);
    }

    static void t(Collection<? extends Super> coll) {
        System.out.println(coll.size());
    }

    static <E> void te(Collection<E> collection) {
        System.out.println(collection.size());
    }
}
