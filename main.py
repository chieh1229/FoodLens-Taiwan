import base64
import io

import cv2
import numpy as np
import uvicorn
from fastapi import FastAPI, File, UploadFile

from FoodRecog import read_image, preprocess, predict
from OCR import paddleocr, draw_ocr

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "HelloWorld"}


@app.post("/food")
async def food_recog(file: UploadFile = File(...)):
    # Read the file uploaded by user
    img_bytes = await file.read()
    image = read_image(img_bytes)
    # after doing preprocessing
    image = preprocess(image)
    pred = predict(image)
    print(pred)
    return {"result": str(pred)}


@app.post("/ocr")
async def ocr_image(file: UploadFile = File(...)):
    image_bytes = await file.read()
    boxes, texts = paddleocr(image_bytes)

    nparr = np.frombuffer(image_bytes, np.uint8)
    image_cv = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    image_cv = cv2.cvtColor(image_cv, cv2.COLOR_BGR2RGB)
    image = draw_ocr(image_cv, boxes)
    image_width, image_height = image.size

    # 将图像转换为base64编码的字符串
    buffered = io.BytesIO()
    image.save(buffered, format='PNG')
    
    img_str = base64.b64encode(buffered.getvalue()).decode()

    return {"image": img_str, "texts": texts, "boxes": boxes, "width": image_width, "height": image_height}


if __name__ == "__main__":
    uvicorn.run(app, host="localhost", port=8000)
