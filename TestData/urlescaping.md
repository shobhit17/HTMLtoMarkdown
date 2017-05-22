
# Markdown\-sensible characters processing
This test checks special characters processing inside URLs: parenthesis and brackets should be escaped to keep markdown image and anchor syntax safe and sound\.

   * [Some MSDN link using parenthesis](http://msdn.microsoft.com/en-us/library/system.drawing.drawing2d(v=vs.110))
   * [Google search result URL with unescaped brackets](https://www.google.ru/search?q=[bracketsarecool])
   * [Yet another test for [brackets], {curly braces} and (parenthesis) processing inside the anchor](https://www.google.ru/search?q='[({})]')
   * Use automatic links like[http://example\.com/](http://example.com/)when the URL is the label
   * Exempt[non\-absolute_URIs](non-absolute_URIs)from automatic link detection
And here are images with tricky attribute values:
![Image](http://placehold.it/350x150#(banana))
![Image](http://placehold.it/350x150#[banana])
![Image](http://placehold.it/350x150#{banana})
![Image](http://placehold.it/350x150#([{}]))