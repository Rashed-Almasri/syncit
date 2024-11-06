package com.syncit.helper;

import lombok.Data;

@Data
public class CodeTemplates {
    String javascriptTemplate =
            "function myFunction() {\n" +
                    "    // Your code here\n" +
                    "}\n" +
                    "\n" +
                    "myFunction();";

    String pythonTemplate =
            "def my_function():\n" +
                    "    # Your code here\n" +
                    "\n" +
                    "if __name__ == '__main__':\n" +
                    "    my_function()";

    String javaTemplate =
            "public class Main {\n" +
                    "    public static void main(String[] args) {\n" +
                    "        // Your code here\n" +
                    "    }\n" +
                    "}";

    String cppTemplate =
            "#include <bits/stdc++.h>\n" +
                    "using namespace std;\n" +
                    "\n" +
                    "int main() {\n" +
                    "    // Your code here\n" +
                    "    return 0;\n" +
                    "}";

    String htmlTemplate =
            "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Document</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <!-- Your content here -->\n" +
                    "</body>\n" +
                    "</html>";

    String cssTemplate =
            "body {\n" +
                    "    /* Your styles here */\n" +
                    "}";

    String jsonTemplate =
            "{\n" +
                    "    \"key\": \"value\"\n" +
                    "}";

    String markdownTemplate =
            "# Title\n" +
                    "\n" +
                    "## Subtitle\n" +
                    "\n" +
                    "- List item 1\n" +
                    "- List item 2\n" +
                    "\n" +
                    "[Link text](https://example.com)\n" +
                    "\n" +
                    "> Blockquote";
}
