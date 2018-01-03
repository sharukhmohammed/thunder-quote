package sharukh.thunderquote;

public class Quote
{
    public String quoteText;
    public String quoteAuthor;
    public String senderName;
    public String senderLink;
    public String quoteLink;

    @Override
    public String toString()
    {
        return quoteText;
    }

    @Override
    public boolean equals(Object quote)
    {
        if (quote != null && quote instanceof Quote)
            return this.quoteText.equals(((Quote) quote).quoteText);
        else return false;
    }

    public String getSummary()
    {
        return quoteText.substring(0, 15).trim() + "...";
    }

    public String getQuoteTextWithDoubleQuotes()
    {
        return "\"" + quoteText.trim() + "\"";
    }

    public String getPrettyQuote()
    {
        return getQuoteTextWithDoubleQuotes() + "\n-" + quoteAuthor;
    }

    public String getPrettyQuoteWithoutQuotations()
    {
        return quoteText+ "\n-" + quoteAuthor;
    }

}