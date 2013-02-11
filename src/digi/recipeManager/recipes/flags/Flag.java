package digi.recipeManager.recipes.flags;

public class Flag<T>
{
	private T		value;
	private String	failMsg;
	private String	successMsg;
	
	public Flag(T value, String failMsg, String succesMsg)
	{
		this.value = value;
		this.failMsg = failMsg;
		this.successMsg = succesMsg;
	}
	
	public T getValue()
	{
		return value;
	}
	
	public void setValue(T value)
	{
		this.value = value;
	}
	
	public String getFailMessage()
	{
		return failMsg;
	}
	
	public void setFailMsg(String failMsg)
	{
		this.failMsg = failMsg;
	}
	
	public String getSuccessMessage()
	{
		return successMsg;
	}
	
	public void setSuccessMsg(String successMsg)
	{
		this.successMsg = successMsg;
	}
}
