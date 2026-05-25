from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Optional

app = FastAPI()
class Car(BaseModel):
    id: Optional[int] = None
    brand: str
db_cars = []

@app.get("/")
def read_root():
    return {"status": "API de Carros rodando!"}

@app.post("/cars", response_model=Car)
def create_car(car: Car):
    new_id = len(db_cars) + 1
    car.id = new_id
    db_cars.append(car)
    
    print(f"Carro recebido do App: {car.brand} (ID gerado: {car.id})")
    return car

@app.get("/cars", response_model=List[Car])
def get_cars():
    return db_cars

@app.delete("/cars/{id}")
def delete_car(id: int):
    global db_cars
    db_cars = [car for car in db_cars if car.id != id]
    print(f"Deletando carro com ID: {id}")
    return {"status": "sucesso"}

@app.put("/cars/{id}", response_model=Car)
def update_car(id: int, car: Car):
    global db_cars
    for item in db_cars:
        if item.id == id:
            item.brand = car.brand
            print(f"Carro {id} editado para: {car.brand}")
            return item
    return {"erro": "Carro não encontrado"}