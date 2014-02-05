
public class Item {

	public static final Item[] items = new Item[10];
	
	static{
		for(int i=0; i<items.length; i++){
			items[i] = new Item("item"+i);
		}
	}
	
	private String name;
	
	public Item(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
}
