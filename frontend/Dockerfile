# Use Node.js LTS Alpine as the base
FROM node:18-alpine AS base
WORKDIR /app

# Install dependencies separately for better caching
COPY package.json package-lock.json ./
RUN npm ci

# Copy the rest of the source
COPY . .

# Set build-time environment variables
ARG NEXT_PUBLIC_GAMESTATE_URL
ARG NEXT_PUBLIC_BACKEND_URL
ENV NEXT_PUBLIC_GAMESTATE_URL=$NEXT_PUBLIC_GAMESTATE_URL
ENV NEXT_PUBLIC_BACKEND_URL=$NEXT_PUBLIC_BACKEND_URL

# Build the Next.js app
RUN npm run build

# Use a fresh minimal image for production
FROM node:18-alpine AS runner
WORKDIR /app

# Set environment variables
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1
ENV PORT=3000
ENV HOSTNAME=0.0.0.0

# Copy only the necessary files from builder
COPY --from=base /app/public ./public
COPY --from=base /app/.next ./.next
COPY --from=base /app/node_modules ./node_modules
COPY --from=base /app/package.json ./package.json

# Expose port and start Next.js
EXPOSE 3000
CMD ["npm", "start"]
