package util;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

public class TreeUtils {

	public static <T>List<T> getAllOfType(ITreeContentProvider content, List<?> rootContent, Class<T> clazz){
//		System.out.print("TreeUtils.getAllOfType()");
//		System.out.println("\t"+clazz.getName());
		List<T> list = new ArrayList<T>();
		addAllOfType(content, rootContent.toArray(), clazz, list);
		return list;
	}
	
	private static <T> void addAllOfType(ITreeContentProvider content, Object[] rootContent, Class<T> clazz, List<T> list){
//		System.out.println("--- add all ---");
		for(Object o: rootContent){
//			System.out.println(o.getClass().getName());
			if(clazz.isInstance(o)){
				if(!list.contains(o)) list.add((T)o);
			}
			if(content.hasChildren(o)) addAllOfType(content, content.getChildren(o), clazz, list);
		}
//		System.out.println("--- added ----");
	}
	
	public static <T> List<T> getSubList(List<?> list, Class<T> clazz, String nameOfGetter){
		List<T> ret = new ArrayList<T>();
		for(Object o: list){
			try{
				T t = (T) o.getClass().getMethod(nameOfGetter).invoke(o);
				if(!ret.contains(t)) ret.add(t);
			}catch(Exception ex){}
		}
		return ret;
	}
}
