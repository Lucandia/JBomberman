from PIL import Image

# Load the image
img = Image.open("src/resources/sprites/bomberman.png")

# Since we cannot directly see the image, we'll assume that the cells are regularly spaced.
# We can estimate the size of each cell by dividing the image's width and height by the number of cells in a row or column.
# For this example, we'll assume there are 8 columns and 4 rows per character, with 4 characters in the sprite sheet.

# Get image size
img_width, img_height = img.size

# We'll estimate the number of columns and rows based on typical sprite sheet layouts
estimated_columns = 23  # This is a common number of frames per animation
estimated_rows = 4  # Based on the description

# Calculate the width and height of each cell
cell_width = img_width // estimated_columns 
cell_height = img_height // estimated_rows

cell_width, cell_height

# Calculate the coordinates of the cell containing the desired sprite
for c in range(10, 20, 2):
    for r in range(3):
        print(c, r)
        x = c * cell_width
        y = r * cell_height

        # Crop the image to get the desired sprite
        sprite = img.crop((x, y, x + cell_width, y + cell_height))

        # Display the sprite
        sprite.show()
