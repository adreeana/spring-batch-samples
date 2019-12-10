package com.adreeana.batch.partitions;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.net.MalformedURLException;

public final class Resources {

  public static String getURL(String classPathLocationPattern) {
    try {
      return new ClassPathResource(classPathLocationPattern).getURL().toExternalForm();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public static Resource getResource(String fileName) {
    try {
      return new UrlResource(fileName);

    } catch (MalformedURLException e) {
      throw new RuntimeException("I/O problems when resolving the input file name.", e);
    }
  }

  public static Resource[] getResources(String locationPattern) {
    try {
      return new
        PathMatchingResourcePatternResolver().getResources("classpath*:" + locationPattern);
    } catch (IOException e) {
      throw new RuntimeException("I/O problems when resolving the input resource pattern.", e);
    }
  }
}
