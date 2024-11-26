import requests
from googletrans import Translator
from transformers import AutoTokenizer, AutoModelForCausalLM
import sympy as sp
import re
import os

# Disable tokenizers parallelism warning
os.environ["TOKENIZERS_PARALLELISM"] = "false"

# Load pre-trained model and tokenizer
tokenizer = AutoTokenizer.from_pretrained("gpt2")
model = AutoModelForCausalLM.from_pretrained("gpt2")


def process_math_expression(math_expr): 
    math_expr = re.sub(r"square\(([^)]+)\)", r"(\1)^2", math_expr)
    math_expr = re.sub(r"power\(([^)]+)\)", r"(\1)^2", math_expr)

    return f"Preprocessed math expression: {math_expr}"
    try:
        solution = sp.sympify(math_expr)  # Use SymPy to evaluate the expression
        numeric_solution = solution.evalf()  # Force numeric evaluation
        
        return f"Solution using SymPy (numeric): {numeric_solution}"
    except Exception as e:
        return f"Error evaluating math expression: {e}"


def internet_search(query):
    API_KEY = "AIzaSyBUYYC3GrNfxytvrIUSzZ-fcwuA_5afPqY"
    SEARCH_ENGINE_ID = "023723732afb04126"

    url = f"https://www.googleapis.com/customsearch/v1?key={API_KEY}&cx={SEARCH_ENGINE_ID}&q={query}"

    response = requests.get(url)
    if response.status_code == 200:
        results = response.json().get("items", [])
        if results:
            # Extract the title and snippet of the top result
            top_result = results[0]
            title = top_result.get("title", "No title")
            snippet = top_result.get("snippet", "No snippet")
            # link = top_result.get("link", "No link")
            
            # if "translate" in query.lower():
            #     match = re.search(r"translate\s+(.*?)\s+into\s+(\w+)",  query, re.IGNORECASE)
            #     target_language = match.group(2).strip()
                
            #     snippet = translate_text(snippet, target_language)
            return f"{snippet}"
        else:
            return "No search results found."
    else:
        return f"Failed to fetch search results. Status code: {response.status_code}"
    

def translate_text(text_to_translate, target_language = 'mn'):
    try:
        translator = Translator()
        translated = translator.translate(text_to_translate, dest=target_language)
        return f"Translated Text: {translated.text}"
    except Exception as e:
        return f"Error during translation: {e}"


def process_question(question):
    if any(op in question for op in ["+", "-", "*", "/", "^", "square", "power"]):
        math_expr = None
        math_expr_match = re.search(r"[a-zA-Z0-9\+\-\*/\^()\.\s]+", question)  # Matches numbers, operators, parentheses, spaces, and functions

        if math_expr_match:
            math_expr = math_expr_match.group(0)
            
            if(math_expr): 
                return process_math_expression(math_expr)
            else: 
                return internet_search(question)
        else:
            return internet_search(question)
    else:
        return internet_search(question)


input_text = input("Enter your question: ")
result = process_question(input_text)

print(result)






# # Function to generate GPT-2 response
# def gpt2_response(input_text):
#     input_ids = tokenizer.encode(input_text, return_tensors="pt")

#     # Set pad_token_id to eos_token_id to avoid warning
#     model.config.pad_token_id = model.config.eos_token_id

#     # Generate the attention mask (since GPT-2 has no padding, all tokens are valid)
#     attention_mask = input_ids.ne(model.config.pad_token_id)  # `ne` checks for "not equal"

#     # Generate the output with the attention mask
#     output = model.generate(input_ids, attention_mask=attention_mask, max_length=100, num_return_sequences=1)
    
#     # Decode and return the result
#     generated_text = tokenizer.decode(output[0], skip_special_tokens=True)
#     print(f"GPT-2's response: {generated_text}")
#     return generated_text


# # input_text = '99905779*5455323'
# # input_text = '99905779 / 54553237^2'
# # input_text = '99905779+5455323 - 5455323 + (5455323/321)'
# # input_text = 'square(99905779) / power(5455323)'
