/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPMDecisionsWeatherService.
 * IPMDecisionsWeatherService is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPMDecisionsWeatherService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPMDecisionsWeatherService.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.ipmdecisions.weather.services;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;

/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@ApplicationPath("")
public class JAXActivator extends Application{
     @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClassesManual(resources);
        return resources;
    }
    
    @Override
    public Set<Object> getSingletons(){
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.getAllowedOrigins().add("*");
        corsFilter.setAllowedMethods("OPTIONS, GET, POST, DELETE, PUT, PATCH");
        Set<Object> singletons = new HashSet<>();
        singletons.add(corsFilter);
        return singletons;
    }
    
    private void addRestResourceClassesManual(Set<Class<?>> resources) {
        resources.add(net.ipmdecisions.weather.services.AmalgamationService.class);
        resources.add(net.ipmdecisions.weather.services.JacksonConfig.class);
        resources.add(net.ipmdecisions.weather.services.MetaDataService.class);
        resources.add(net.ipmdecisions.weather.services.QualityControlService.class);
        resources.add(net.ipmdecisions.weather.services.TestDataService.class);
        resources.add(net.ipmdecisions.weather.services.WeatherAdapterService.class);
        resources.add(net.ipmdecisions.weather.services.WeatherDataSourceService.class);
    }

    /**
     * This method is being auto populated by NetBeans, which often blows up the application
     * @param resources 
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.webcohesion.enunciate.rt.EnunciateJaxbContextResolver.class);
        resources.add(net.ipmdecisions.weather.services.AmalgamationService.class);
        resources.add(net.ipmdecisions.weather.services.JacksonConfig.class);
        resources.add(net.ipmdecisions.weather.services.MetaDataService.class);
        resources.add(net.ipmdecisions.weather.services.QualityControlService.class);
        resources.add(net.ipmdecisions.weather.services.TestDataService.class);
        resources.add(net.ipmdecisions.weather.services.WeatherAdapterService.class);
        resources.add(net.ipmdecisions.weather.services.WeatherDataSourceService.class);
        resources.add(org.jboss.resteasy.core.AcceptHeaderByFileSuffixFilter.class);
        resources.add(org.jboss.resteasy.core.AsynchronousDispatcher.class);
        resources.add(org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter.class);
        resources.add(org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor.class);
        resources.add(org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor.class);
        resources.add(org.jboss.resteasy.plugins.interceptors.MessageSanitizerContainerResponseFilter.class);
        resources.add(org.jboss.resteasy.plugins.providers.AsyncStreamingOutputProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.ByteArrayProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.DataSourceProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.DefaultBooleanWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.DefaultNumberWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.DefaultTextPlain.class);
        resources.add(org.jboss.resteasy.plugins.providers.DocumentProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.FileProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.FileRangeWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.IIOImageProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.InputStreamProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.JaxrsFormProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.JaxrsServerFormUrlEncodedProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.MultiValuedParamConverterProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.ReaderProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.SourceProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.StreamingOutputProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.StringTextStar.class);
        resources.add(org.jboss.resteasy.plugins.providers.sse.SseEventProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.sse.SseEventSinkInterceptor.class);

    }
}
