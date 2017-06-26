package com.earldouglas.directory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

public class WsdlGenerator {
	
	public static void main(String[] arguments) throws Exception {
		DefaultWsdl11Definition defaultWsdl11Definition = new DefaultWsdl11Definition();
		defaultWsdl11Definition.setPortTypeName("Directory");
		defaultWsdl11Definition.setLocationUri("/");
		defaultWsdl11Definition
				.setTargetNamespace("http://www.earldouglas.com/directory");

		SimpleXsdSchema simpleXsdSchema = new SimpleXsdSchema();
		simpleXsdSchema.setXsd(new ClassPathResource(
				"com/earldouglas/directory/directory.xsd"));
		simpleXsdSchema.afterPropertiesSet();

		defaultWsdl11Definition.setSchema(simpleXsdSchema);

		defaultWsdl11Definition.afterPropertiesSet();
		System.out.println(defaultWsdl11Definition.getSource());
	}
}
