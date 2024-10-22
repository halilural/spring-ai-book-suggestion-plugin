import kagglehub

# Download latest version
path = kagglehub.dataset_download("bilalyussef/google-books-dataset")

print("Path to dataset files:", path)