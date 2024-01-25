const uuid = require('uuid')

const uuidGenerator = () => {
    const val = uuid.v4();
    return val
}

export default uuidGenerator