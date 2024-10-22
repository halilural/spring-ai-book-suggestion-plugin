import pandas as pd
import psycopg2
from sentence_transformers import SentenceTransformer

# Load the CSV file
csv_file = "dataset.csv"
df = pd.read_csv(csv_file)

# Initialize a pre-trained embedding model (e.g., all-MiniLM-L6-v2)
model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')

# Database connection details
db_config = {
    "host": "localhost",
    "port": 5432,
    "user": "app_user",
    "password": "123456",
    "dbname": "postgres"
}

# Function to insert data into the book table
def insert_book_data(row):
    try:
        # Connect to the YugabyteDB
        conn = psycopg2.connect(**db_config)
        cursor = conn.cursor()
        
        # Convert description to embedding
        description_embedding = model.encode(row['description']).tolist()
        
        # Insert query
        insert_query = """
        INSERT INTO book (title, author, rating, voters, price, currency, description, description_vector, publisher, page_count, generes, ISBN, language, published_date)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        
        # Prepare data
        data = (
            row['title'],
            row['author'],
            row['rating'],
            row['voters'],
            row['price'],
            row['currency'],
            row['description'],
            description_embedding,  # Vector embedding
            row['publisher'],
            row['page_count'],
            row['generes'],
            row['ISBN'],
            row['language'],
            row['published_date']
        )
        
        # Execute the query
        cursor.execute(insert_query, data)
        
        # Commit the transaction
        conn.commit()
        
        print(f"Inserted book: {row['title']}")
        
    except Exception as e:
        print(f"Error inserting book: {row['title']}, Error: {e}")
    finally:
        # Close the connection
        cursor.close()
        conn.close()

# Iterate over the CSV rows and insert data
for index, row in df.iterrows():
    insert_book_data(row)