package com.iwill.deploy.common.utils.spring;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ResourceUtil {
    private static final String CLASSFILE_SUFFIX = "**/*.class";
    private static PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    private static CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourceResolver);

    public static List<MetadataReader> resourceScan(String patternPackage) throws IOException {
        List<MetadataReader> metadataReaders = new LinkedList<>();
        String location = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(patternPackage) +
                CLASSFILE_SUFFIX;
        Resource[] resources = resourceResolver.getResources(location);

        for (Resource resource : resources) {
            metadataReaders.add(metadataReaderFactory.getMetadataReader(resource));
        }
        return metadataReaders;
    }

    public static File getFile(String classpathFile) throws IOException {
        return new ClassPathResource(classpathFile).getFile();
    }

}
